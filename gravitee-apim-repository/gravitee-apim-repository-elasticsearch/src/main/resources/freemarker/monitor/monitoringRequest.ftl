<#ftl output_format="JSON">
{
   "size":1,
   "query":{
      "bool":{
         "must":{
            "term":{
               "gateway":"${gateway}"
            }
         }
      }
   },
   "sort":[
      {
         "@timestamp":{
            "order":"desc"
         }
      }
   ]
}