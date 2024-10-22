FROM php:8.3-apache

RUN a2enmod rewrite

RUN apt-get update && apt-get install -y librdkafka-dev && rm -rf /var/lib/apt/lists/*

RUN pecl install rdkafka && docker-php-ext-enable rdkafka

WORKDIR /var/www/html

COPY . /var/www/html/

RUN chown -R www-data:www-data /var/www/html/data

RUN chmod -R 775 /var/www/html/data

EXPOSE 80
