#!/usr/bin/env python3

import subprocess
import json
import sys
from datetime import datetime
import argparse
import threading

COLOR_RESET = "\033[0m"
COLOR_KEY = "\033[34m"
COLOR_STRING = "\033[32m"
COLOR_INT = "\033[33m"
COLOR_ERROR = "\033[31m"
COLOR_WARN = "\033[33m"
COLOR_INFO = "\033[32m"

TOPICS = ["person-api-log", "bank-api", "account-api", "transactions"]

def format_timestamp(timestamp):
    try:
        return datetime.fromtimestamp(timestamp).strftime('%Y-%m-%d %H:%M:%S')
    except:
        return str(timestamp)

def colorize(data, level_color=None):
    if isinstance(data, dict):
        print("{")
        for i, (key, value) in enumerate(data.items()):
            comma = "," if i < len(data) - 1 else ""

            if key == "timestamp" and isinstance(value, int):
                value = format_timestamp(value)

            print(f"  {COLOR_KEY}\"{key}\"{COLOR_RESET}: ", end="")
            colorize(value, level_color)
            print(comma)
        print("}")
    elif isinstance(data, list):
        print("[")
        for i, item in enumerate(data):
            comma = "," if i < len(data) - 1 else ""
            colorize(item, level_color)
            print(comma)
        print("]")
    elif isinstance(data, str):
        color = level_color if level_color else COLOR_STRING
        print(f"{color}\"{data}\"{COLOR_RESET}", end="")
    elif isinstance(data, int):
        print(f"{COLOR_INT}{data}{COLOR_RESET}", end="")
    else:
        print(f"{data}", end="")

def format_message(line):
    try:
        json_data = json.loads(line)

        level_color = None
        if "level" in json_data:
            level = json_data["level"].lower()
            if level == "error":
                level_color = COLOR_ERROR
            elif level == "warn":
                level_color = COLOR_WARN
            elif level == "info":
                level_color = COLOR_INFO

        colorize(json_data, level_color)
        print()
    except json.JSONDecodeError:
        print(f"{COLOR_ERROR}Failed to parse JSON: {line}{COLOR_RESET}")

def consume_kafka_topic(topic):
    try:
        print(f"Listening to topic: {topic}")
        process = subprocess.Popen(
            ["docker", "exec", "kafka", "kafka-console-consumer.sh", 
            "--bootstrap-server", "kafka:9092", "--topic", topic, "--from-beginning"],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            universal_newlines=True,
            bufsize=1
        )

        while True:
            output = process.stdout.readline()
            if output:
                format_message(output.strip())
            elif process.poll() is not None:
                break

        error_output = process.stderr.read()
        if error_output:
            print(f"{COLOR_ERROR}Error from Kafka process: {error_output}{COLOR_RESET}")

    except Exception as e:
        print(f"Error: {e}")
    finally:
        process.stdout.close()
        process.stderr.close()


def consume_kafka(topics):
    threads = []

    for topic in topics:
        thread = threading.Thread(target=consume_kafka_topic, args=(topic,))
        thread.start()
        threads.append(thread)

    for thread in threads:
        thread.join()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Consume Kafka topics and format messages.")
    parser.add_argument("-t", "--topic", help="Specify a single topic to consume.")

    args = parser.parse_args()

    if args.topic:
        consume_kafka([args.topic])
    else:
        consume_kafka(TOPICS)
