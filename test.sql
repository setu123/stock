
select h1.code, h1.date, h2.date, h1.last_updated, h1.director, h1.government, h1.institute, h1.forein, h1.public, h2.public 
from share_holding_history h1, share_holding_history h2
where h1.code = h2.code
and h1.public < h2.public
and h1.date > h2.date
order by (h1.public - h2.public)