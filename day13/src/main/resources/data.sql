insert into lxh_users
    (id,username,name,password_hash,email,enabled,account_non_expired,account_non_locked,credentials_non_expired)
    values
     (1,'user1','zhangSan','{bcrypt}$2a$10$n8/JPcFt8jZM6479XvFnQe07ppeDRok4D.kXe3JQX3Y3DsY04JyN2','18569413587@163.com',true,true,true,true),
     (2,'user2','lisi','{SHA-1}{NJrgZSWGJjI8XU6B/uAf3zGNsILyQzMkFvMOPtuVAdM=}984325e3920910d7a0a2baabed434b224284e26f','18569413589@163.com',true,true,true,true);
insert into lxh_roles (id,role_name) values
     (1,'ROLE_USER'),
     (2,'ROLE_ADMIN');
insert into lxh_users_roles (user_id,role_id) values (1,1),(1,2),(2,2);
