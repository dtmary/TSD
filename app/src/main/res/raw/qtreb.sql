select t.pki, t.cnt, pk.namepki from skladuser.temp_treb t,
              skladuser.pki pk
where t.pki = pk.pki