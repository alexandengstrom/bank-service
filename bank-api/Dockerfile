FROM rust:1.81 as builder

RUN apt-get update && apt-get install -y \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN cargo install diesel_cli --no-default-features --features postgres

COPY . .

RUN cargo build --release

FROM debian:bookworm

RUN apt-get update && apt-get install -y \
    libpq-dev \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/release/bank-api /usr/local/bin/bank-api

COPY --from=builder /app/migrations /app/migrations

COPY --from=builder /usr/local/cargo/bin/diesel /usr/local/bin/diesel

COPY wait-for-it.sh /usr/local/bin/wait-for-it.sh

WORKDIR /app

RUN chmod +x /usr/local/bin/wait-for-it.sh

EXPOSE 8000

CMD ["sh", "-c", "/usr/local/bin/wait-for-it.sh db:5432 -- diesel migration run && bank-api"]
