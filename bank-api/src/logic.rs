use crate::{db::establish_connection, models::Bank};
use diesel::prelude::*;

pub fn list_all_banks() -> Vec<Bank>{
    use crate::schema::banks::dsl::*;

    let connection = &mut establish_connection();
    let result = banks
        .load::<Bank>(connection)
        .expect("Error showing banks");

    result

}

pub fn find_bank_by_name(bank_name: &str) -> Vec<Bank> {
    use crate::schema::banks::dsl::*;

    let connection = &mut establish_connection();
    let results = banks
        .filter(name.eq(bank_name))
        .load::<Bank>(connection)
        .expect("Error finding banks by name");

    results
}


pub fn find_bank_by_id(bank_id:i32) -> Bank{
    use crate::schema::banks::dsl::*;

    let connection = &mut establish_connection();
    let result = banks
        .filter(key.eq(bank_id))
        .first(connection)
        .expect("Errow finding bank by name");

    result
   
}