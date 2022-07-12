select dl.opnum_users_data,
  (select us.login
  from skladuser.users us
  where us.id=dl.id_users) as login
  from skladuser.users_data_login dl
  where sysdate between dl.data_n and dl.data_k
    and dl.opnum_users_data in (select u.opnum
    from skladuser.users_data u
    where u.tab_n=:tabn)