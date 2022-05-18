select v.PKI_CHG, v.QTY_CHG as cntzam,
       mfg.Get_Cell_Pki(pk.pki, :sklad, 0) as cell,
       pk.namepki
  from skladuser.vedom_zam v,
       skladuser.pki pk
where v.PKI_CHG = pk.pki
  and v.pki_sost = :pki