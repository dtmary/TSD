--статика
declare
  v_opnum skllog.opnum%type;
  v_docnum skllog.docnum%type;
  v_makeresult varchar2(255);
  v_saveresult boolean;
  cntrec number;
  v_docdate varchar2(10);
  v_docform varchar2(255);
  v_company_id number;
  v_kodoper number;

  v_skladin varchar2(5);
  v_skladout varchar2(5);
  v_iscalcnal_r number;
  v_in_calcwithreserv number;
  v_folder varchar2(10);

  v_ppkib_rec pcgsklad.ppkib_rec_type;
  v_ppki_rec pcgsklad.ppki_rec_type;
  v_pcshid_rec pcgsklad.pcshid_rec_type;
  v_postatok_rec pcgsklad.postatok_rec_type;
  v_pitem_count_rec pcgsklad.pitem_count_rec_type;
  v_pprice_rec pcgsklad.pprice_rec_type;
  v_psumma_rec pcgsklad.psumma_rec_type;
  v_paccd_rec pcgsklad.paccd_rec_type;
  v_paccc_rec pcgsklad.paccc_rec_type;
  v_pmg_nbr_rec pcgsklad.pmg_nbr_rec_type;
  v_pmg_lot_rec pcgsklad.pmg_lot_rec_type;
  v_ptyp_pkib_rec pcgsklad.ptyp_pkib_rec_type;
  v_pspz_rec pcgsklad.pspz_rec_type;
  i integer;
begin
  v_docdate := trunc(sysdate);
  v_docform := fonds.readsetting(in_sect => 'skl_DocForms',in_ident => 'RsxSkl',in_company_id => 1,in_loginid => 0);
  v_company_id := 1;
  v_kodoper := 133;
  v_opnum := 0;
  cntrec := 0;
  v_docnum := 0;
  v_iscalcnal_r := 0;
  v_in_calcwithreserv := 0;

--------

  &macroparams

 ---------------------------------------

  v_makeresult := pcgsklad.makepkibskl(in_docdate => v_docdate,
                                   in_opnum => 0,
                                   in_skladin => v_skladin,
                                   in_calcwithreserv => v_in_calcwithreserv,
                                   in_company_id => v_company_id,
                                   iscalcnal_r => v_iscalcnal_r,
                                   in_kodoper => v_kodoper);

  i := 0;
  for rec in (select * from skladuser.pkibsklraspres p) loop
    i := i + 1;
    v_ppkib_rec(i) := rec.pkib;
    v_ppki_rec(i) := rec.pki;
    v_pcshid_rec(i) := rec.schid;
    v_postatok_rec(i) := 0;
    v_pitem_count_rec(i) := rec.item_count;
    v_pprice_rec(i) := rec.price;
    v_psumma_rec(i) := rec.summa;
    v_paccd_rec(i) := rec.accd;
    v_paccc_rec(i) := rec.accc;
    v_pmg_nbr_rec(i) := rec.mg_nbr;
    v_pmg_lot_rec(i) := rec.mg_lot;
    v_ptyp_pkib_rec(i) := 0;
    v_pspz_rec(i) := rec.spz;
  end loop;

  v_saveresult := pcgsklad.savesklrsx(in_action => 0,
                                  in_opnum => v_opnum,
                                  in_docdate => v_docdate,
                                  in_docnum => v_docnum,
                                  in_company_id => v_company_id,
                                  in_skladout => v_skladin,
                                  in_skladin => v_skladout,
                                  in_oper => v_kodoper,
                                  in_user_id => 'TSD',
                                  is_otlog => 'N',
                                  in_par_opnum => null,
                                  in_kod_post => null,
                                  in_docfrm => v_docform,
                                  in_cbfolder => null,
                                  in_edtfolder => v_folder,
                                  in_skl_rec_count => cntrec,
                                  ppkib_rec => v_ppkib_rec,
                                  ppki_rec => v_ppki_rec,
                                  pcshid_rec => v_pcshid_rec,
                                  postatok_rec => v_postatok_rec,
                                  pitem_count_rec => v_pitem_count_rec,
                                  pprice_rec => v_pprice_rec,
                                  psumma_rec => v_psumma_rec,
                                  paccd_rec => v_paccd_rec,
                                  paccc_rec => v_paccc_rec,
                                  pmg_nbr_rec => v_pmg_nbr_rec,
                                  pmg_lot_rec => v_pmg_lot_rec,
                                  ptyp_pkib_rec => v_ptyp_pkib_rec,
                                  pspz_rec => v_pspz_rec);
end;
