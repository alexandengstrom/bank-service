// @generated automatically by Diesel CLI.

diesel::table! {
    banks (key) {
        key -> Int4,
        #[max_length = 100]
        name -> Varchar,
    }
}
