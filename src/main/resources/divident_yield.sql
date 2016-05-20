
select pressure.code, closeprice(pressure.last_price, pressure.close_price) as closePrice, statistic.yearEnd, history.date, history.percent,
(history.percent*10)/closeprice(pressure.last_price, pressure.close_price) as yield
from (select * from bs_pressure where date = (select max(date) from bs_pressure)) as pressure, divident_history history, year_statistics statistic
where pressure.code = history.code
and pressure.code = statistic.code
and (history.date like '2015-03%' or history.date like '2015-04%')
and statistic.yearEnd like '%12'
and history.type = 'CASH'
and (history.percent*10)/closeprice(pressure.last_price, pressure.close_price) >= 5
and statistic.pe <10 and statistic.pe > 0
order by yield desc;