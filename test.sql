
select h1.code, h1.date, h2.date, h1.last_updated, h1.director, h1.government, h1.institute, h1.forein, h1.public, h2.public 
from share_holding_history h1, share_holding_history h2
where h1.code = h2.code
and h1.public < h2.public
and h1.date = '2017-01-31' and h2.date = '2016-12-29'
order by (h1.public - h2.public)




select * from share_holding_history s2
where 
1=1

#code = 'dbh'

and id < (select max(id) from share_holding_history s1
where code = s2.code
and date = (select date from share_holding_history
where code = s1.code
group by date
having count(date) > 1)) 

and date = (select date from share_holding_history
where code = s2.code
group by date
having count(date) > 1) 
