use std::collections::HashMap;
use actix_web::{get, web, App, HttpResponse, HttpServer, Responder};
use env_logger;
use kafka::send_kafka_message;
use logic::{find_bank_by_id, find_bank_by_name, list_all_banks};
use serde_json::json;

mod kafka;
mod db;
mod schema;
mod models;
mod logic;

async fn not_found() -> impl Responder {
    HttpResponse::NotFound().json(
        json!({
            "message": "Sorry, this page you are looking for does not exist! Try /bank"
        })
    )
}


#[get("")]
async fn find_by_name(web::Query(params): web::Query<HashMap<String, String>>) -> impl Responder {
    if let Some(bank_name) = params.get("name") {
        // Search for bank by name
        let bank_name = bank_name.clone();
        let banks = web::block(move || find_bank_by_name(&bank_name)).await;

        actix_web::rt::spawn(async {
            send_kafka_message("Letar efter bank med namnet").await;
        });

        match banks {
            Ok(banks) => HttpResponse::Ok().json(json!(banks)),
            Err(_) => HttpResponse::InternalServerError().json(json!({
                "error": "Failed to find bank, try again later"
            })),
        }
    } else {
        // List all banks if "name" parameter is not provided
        match web::block(move || list_all_banks()).await {
            Ok(banks) => {
                actix_web::rt::spawn(async {
                    send_kafka_message("Lista av banker skickades").await;
                });
                HttpResponse::Ok().json(banks)
            },
            Err(_) => HttpResponse::InternalServerError().json(json!({
                "error": "Failed to fetch banks, try again later"
            })),
        }
    }
}

#[get("/{id}")]
async fn find_by_id(path: web::Path<i32>) -> impl Responder {
    let bank_id = path.into_inner();

    let bank = web::block(move || find_bank_by_id(bank_id)).await;

    actix_web::rt::spawn(async {
        send_kafka_message("Letar efter bank med id").await;
    });

    match bank {
        Ok(bank) => HttpResponse::Ok().json(json!({"bank": bank})),
        Err(_) => HttpResponse::InternalServerError().json(json!({
            "error": "Failed to find bank, try again later"
        })),
    }
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    env_logger::init();
    HttpServer::new(|| {
        App::new()
            .service(
                web::scope("/bank")
                    .service(find_by_name)
                    .service(find_by_id)
            )
            .default_service(
                web::route().to(not_found)
            )
    })
    .bind(("0.0.0.0", 8000))?
    .run()
    .await
}
