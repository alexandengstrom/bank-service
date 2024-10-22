<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

define('DATA_DIR', __DIR__ . '/data/');
define('KAFKA_BROKER', 'kafka:9092');
define('KAFKA_TOPIC', 'person-api-log');

logToKafka('info', 'Incoming request', $_SERVER['REQUEST_METHOD'], $_SERVER['REQUEST_URI']);

$method = $_SERVER['REQUEST_METHOD'];
$uri = trim($_SERVER['REQUEST_URI'], '/');

$parsed_url = parse_url($uri);
$path = isset($parsed_url['path']) ? trim($parsed_url['path'], '/') : '';

$parts = explode('/', $path);

$entity = isset($parts[0]) ? $parts[0] : null;
$id = isset($parts[1]) ? $parts[1] : null;

switch ($method) {
    case 'GET':
        handleGet($entity, $id);
        break;
    default:
        logToKafka('warn', 'Method not allowed', $method, $uri);
        http_response_code(405);
        echo json_encode(["error" => "Method not allowed"]);
        break;
}

function readData($entity) {
    $file = DATA_DIR . "$entity.json";
    if (!file_exists($file)) {
        logToKafka('warn', 'File not found', $_SERVER['REQUEST_METHOD'], $_SERVER['REQUEST_URI'], $entity);
        return [];
    }
    $json = file_get_contents($file);
    return json_decode($json, true);
}

function handleGet($entity, $id) {
    if (!$entity || !file_exists(DATA_DIR . "$entity.json")) {
        logToKafka('error', 'Entity not specified or does not exist', $_SERVER['REQUEST_METHOD'], $_SERVER['REQUEST_URI'], $entity);
        http_response_code(400);
        echo json_encode(["error" => "Entity not specified or does not exist"]);
        return;
    }

    $data = readData($entity);

    if ($id === null) {
        if (!empty($_GET)) {
            $result = array_filter($data, function($item) {
                foreach ($_GET as $key => $value) {
                    if (!isset($item[$key])) {
                        return false;
                    }
                    if (is_string($item[$key])) {
                        if (strtolower($item[$key]) != strtolower($value)) {
                            return false;
                        }
                    } else {
                        if ($item[$key] != $value) {
                            return false;
                        }
                    }
                }
                return true;
            });

            echo json_encode(array_values($result));
            return;
        }

        echo json_encode($data);
        return;
    }

    foreach ($data as $item) {
        if ($item['key'] == $id) {
            echo json_encode($item);
            return;
        }
    }

    logToKafka('warn', 'Item not found', $_SERVER['REQUEST_METHOD'], $_SERVER['REQUEST_URI'], $entity, $id);
    http_response_code(404);
    echo json_encode(["error" => "Not found"]);
}

function logToKafka($level, $message, $method, $uri, $entity = null, $id = null) {
    $config = new RdKafka\Conf();
    $config->set('metadata.broker.list', 'kafka:9092');

    $producer = new RdKafka\Producer($config);
    if (!$producer) {
        throw new Exception("Failed to create Kafka producer");
    }

    $topic = $producer->newTopic(KAFKA_TOPIC);
    if (!$topic) {
        throw new Exception("Failed to create Kafka topic");
    }

    $logMessage = json_encode([
        'level' => $level,
        'message' => $message,
        'method' => $method,
        'uri' => $uri,
        'timestamp' => time()
    ]);

    $topic->produce(RD_KAFKA_PARTITION_UA, 0, $logMessage);

    $producer->poll(0);

    $maxRetries = 10;
    while ($producer->getOutQLen() > 0 && $maxRetries > 0) {
        $producer->poll(100);
        $maxRetries--;
    }

    if ($producer->getOutQLen() > 0) {
        throw new Exception("Failed to deliver Kafka message after retries");
    }
}
