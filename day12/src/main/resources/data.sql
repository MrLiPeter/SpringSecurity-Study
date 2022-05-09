insert into lxh_users
    (username,password,enabled)
    values
     ('user','{bcrypt}$2a$10$n8/JPcFt8jZM6479XvFnQe07ppeDRok4D.kXe3JQX3Y3DsY04JyN2',1),
     ('lisi','{SHA-1}{NJrgZSWGJjI8XU6B/uAf3zGNsILyQzMkFvMOPtuVAdM=}984325e3920910d7a0a2baabed434b224284e26f',1);
insert into lxh_authorities (username,authority) values
    ('user','ROLE_USER'),
    ('user','ROLE_ADMIN'),
    ('lisi','ROLE_USER');
