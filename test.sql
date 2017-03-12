
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


#Find most purchase in last week from stockbangladesh portfolios
select st_ban.code, marchent_count, lw_quantity, last_week_buy, tradable, (lw_quantity/tradable)*100 as percentage, ids  from
(
SELECT code, count(code) as marchent_count, sum(quantity) as lw_quantity, sum(quantity*buy_price) as last_week_buy, group_concat(main.remote_id) as ids
FROM stock.merchant_portfolio_details details, merchant_portfolio main
where date > '2017-03-06'
and quantity > 1
and main.id = details.portfolio
group by code
order by count(code) desc
) as st_ban, 
(
select code, ((institute+forein+public)*totalSecurity)/100  as tradable from year_statistics
) as trade_able
where st_ban.code = trade_able.code
order by percentage desc
