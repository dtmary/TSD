declare
  cntrec number;
begin
  cntrec := 0;
  insert into pkibsklrasp(pki, item_count, mg_nbr, mg_lot, recid, spz, accc, accd)values('ЦАКТ303356001011', 2, '12782963', '12790056', 2, '2000101', null, null);

  &macro1

  insert into pkibsklrasp(pki, item_count, mg_nbr, mg_lot, recid, spz, accc, accd)values('ЦАКТ303356001011', 2, '12782963', '12790056', 2, '2000101', null, null);
end;