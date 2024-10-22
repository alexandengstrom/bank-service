use diesel::prelude::*;
use serde::Serialize;

#[derive(Queryable, Selectable, Serialize)]
#[diesel(table_name = crate::schema::banks)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct Bank{
    pub key: i32,
    pub name: String,
}

