create table if not exists employee (
    id         int auto_increment primary key,
    age        int          null,
    company_id int          null,
    gender     varchar(255) null,
    name       varchar(255) null,
    salary     double          null,
    is_active     boolean          null,
    foreign key (company_id) references company (id)
    );