# MongoDynamo_querytest
Dynamo 인덱스 정보

<span style="color:#333333">활성인덱스는 아래 2개 입니다.</span><br>
<br>
<span style="color:#333333">    1. <span style="color:#000000">gidx\_article\_id\_feed\_id</span></span><br>
<span style="color:#333333">            파티션키    article_id (번호)</span><br>
<span style="color:#333333">            정렬키       feed_id (문자열)</span><br>
<span style="color:#333333">            속성          user\_id\, feed\_id\, article\_id\, scraped\, article\_type</span><br>
<br>
<span style="color:#333333">    2. <span style="color:#000000">gidx\_article\_id\_feed\_id</span></span><br>
<span style="color:#333333">            파티션키    article_id (번호)</span><br>
<span style="color:#333333">            정렬키       scraped (번호)</span><br>
<span style="color:#333333">            속성          user\_id\, feed\_id\, article\_id\, scraped\, user\_id\, article\_id\, scraped\, article\_type\, feed\_id</span><br>
<br>
<span style="color:#222222">DynamoDB는 다음과 같이 두 가지 종류의 인덱스를 지원합니다.</span><br>
[<u><span style="color:#0066cc">https://docs.aws.amazon.com/ko_kr/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html</span></u>](https://docs.aws.amazon.com/ko_kr/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html)<br>
<br>
Global secondary index – 파티션 키 및 정렬 키가 테이블의 파티션 키 및 정렬 키와 다를 수 있는 인덱스입니다. <br> ( nbase-t  Global Index 개념)

로컬 보조 인덱스 – 테이블과 파티션 키는 동일하지만 정렬 키는 다른 인덱스입니다. <br>( 파티션키 (==샤딩키) 를 기본으로 추가되는 다른 인덱스 )

<br>


## mongodb 인덱스 생성 Test

Mongo DB Test 환경<br><br>
스토리지 크기      : 0.72 GB<br>
항목 수           : 14,912,482<br>

<span style="color:#333333">1) article_id , scraped</span>
<span style="color:#333333"> 생성 소요시간 : 72.8 초</span>

<span style="color:#333333">2\) article\_id \, feed\_id</span>
<span style="color:#333333"> 생성 소요시간 : 79.2 초</span>

<span style="color:#333333">3\) article\_id \, scraped \, user\_id\,  feed\_id\,  article\_type</span>
<span style="color:#333333"> 생성 소요시간 : 103 초</span>

4\) article\_id\, user\_id
<span style="color:#333333"> 생성 소요시간 : 73. 8 초</span>
<br>


## AWS Dynamo 조회 시간 / mongodb 조회시간 비교 Test

AWS Dynamo 조회시간 / mongoDB 조회시간

1\. article\_id\, feed\_id<br>
query where 조건 : article\_id\, feed\_id<br><br>
MongoDB 조회 소요시간 : 0.0032 초<br>
DynamoDB 조회 소요시간 : 0.0021 초<br><br>

2\. article\_id\, feed\_id\, user\_id\, scraped\, article\_type<br>
query where 조건 : article\_id\, user\_id\, feed\_id\, article\_type<br><br>
MongoDB 조회 소요시간 : 0.0062 초<br><br>

3\. article\_id\, user\_id<br>
query where 조건 : article\_id\, user\_id<br><br>
MongoDB 조회 소요시간 : 0.0026 초<br>
DynamoDB 조회 소요시간 : 0.0023 초<br>
<span style="color:#0a0a0a"> </span>

## <span style="color:#0a0a0a">JAVA에서의 DynamoDB Query문 </span>

DynamoDB

DynamoDB에서는 쿼리문에 있어서 파티션 키 값이 되는 항목이 무조건 들어가야합니다.
그리고 파티션 키, 정렬 키에 대한 조건 문과 나머지 속성 값에 대한 조건 문을 따로 설정하게 되어 있었습니다.
Index를 타게하고 싶은 경우에는 해당 Index를 직접 지정해야하며, 그 Index의 키를 따라서 쿼리문을 작성해야합니다.

Table에 직접 Query를 실행시키는 소스
1
원하는 Index를 지정해서 Query를 실행시키는 소스
2

## <span style="color:#0a0a0a"> 인덱스 조회 / 생성 / 삭제 </span>

```
# collection 의 모든 인덱스 조회
db.collection.getIndexes()

# 오름차순 (ASC) 인덱스 생성시
db.collection.createIndex( { field : 1 } )

# 내림차순 (DES) 인덱스 생성시
db.collection.createIndex( { field : -1 } )

# fistNAme 과 lastName 의 복합인덱스에 unqiue 속성 적용시
db.collection.createIndex( { firstName : 1 , lastName:1  }  ,  { unique : true } )

# nodate 가 현재시간과 1시간 이상 차이나면 제거 하는 인덱스 (단위:초)
db.collection.createIndex( { "nowdate" : 1 } ,{  expireAfterSeconds :  3600  } )

# 인덱스 삭제
db.collection.dropindex( { field:1 } )

```

## <span style="color:#0a0a0a">실행 계획 확인 </span>

```
`# 테스트 데이터 생성`
> for(var n=0 ; n <= 100000 ; n++) db.numbers.save({n:n, m: "test"}) ;
WriteResult({ "nInserted" : 1 })
> db.numbers.count()
100001
>



# 쿼리 실행계획 확인
# plan 확인시 콜렉션 scan 을 하고 있음  "stage" : "COLLSCAN",
db.numbers.find( {n:{"$gt": 5000, "$lt" : 50010}} ).explain()

{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "numbers.numbers",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "$and" : [
                                {
                                        "n" : {
                                                "$lt" : 50010
                                        }
                                },
                                {
                                        "n" : {
                                                "$gt" : 5000
                                        }
                                }
                        ]
                },
                "winningPlan" : {
                        "stage" : "COLLSCAN",
                        "filter" : {
                                "$and" : [
                                        {
                                                "n" : {
                                                        "$lt" : 50010
                                                }
                                        },
                                        {
                                                "n" : {
                                                        "$gt" : 5000
                                                }
                                        }
                                ]
                        },
                        "direction" : "forward"
                },
                "rejectedPlans" : [ ]
        },
        "ok" : 1
}

# 인덱스 조회
> db.numbers.getIndexes()
[
        {
                "v" : 2,
                "key" : {
                        "_id" : 1
                },
                "name" : "_id_",
                "ns" : "numbers.numbers"
        }




# 인덱스 생성
> db.numbers.createIndex ( {n:1} )
{
        "createdCollectionAutomatically" : false,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}

> db.numbers.getIndexes() ;
[
        {
                "v" : 2,
                "key" : {
                        "_id" : 1
                },
                "name" : "_id_",
                "ns" : "numbers.numbers"
        },
        {
                "v" : 2,
                "key" : {
                        "n: 1
                },
                "name" : "n_1",
                "ns" : "numbers.numbers"
        }
]
>

# 인덱스 생성후 plan 확인
# plan 확인시 콜렉션 ixscan 을 하고 있음    "stage" : "IXSCAN",
> db.numbers.find( {n:{"$gt": 5000, "$lt" : 50010}} ).explain() ;
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "numbers.numbers",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "$and" : [
                                {
                                        "n" : {
                                                "$lt" : 50010
                                        }
                                },
                                {
                                        "n" : {
                                                "$gt" : 5000
                                        }
                                }
                        ]
                },
                "winningPlan" : {
                        "stage" : "FETCH",
                        "inputStage" : {
                                "stage" : "IXSCAN",
                                "keyPattern" : {
                                        "n" : 1
                                },
                                "indexName" : "n_1",
                                "isMultiKey" : false,
                                "multiKeyPaths" : {
                                        "n" : [ ]
                                },
                                "isUnique" : false,
                                "isSparse" : false,
                                "isPartial" : false,
                                "indexVersion" : 2,
                                "direction" : "forward",
                                "indexBounds" : {
                                        "n" : [
                                                "(5000.0, 50010.0)"
                                        ]
                                }
                        }
                },
                "rejectedPlans" : [ ]
        },
        "ok" : 1
}

# 인덱스  hint 사용
> db.numbers.find({n:{"$gt": 5000, "$lt" : 50010}}).hint( {"_id" : 1} ).explain()
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "numbers.numbers",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "$and" : [
                                {
                                        "n" : {
                                                "$lt" : 50010
                                        }
                                },
                                {
                                        "n" : {
                                                "$gt" : 5000
                                        }
                                }
                        ]
                },
                "winningPlan" : {
                        "stage" : "FETCH",
                        "filter" : {
                                "$and" : [
                                        {
                                                "n" : {
                                                        "$lt" : 50010
                                                }
                                        },
                                        {
                                                "n" : {
                                                        "$gt" : 5000
                                                }
                                        }
                                ]
                        },
                        "inputStage" : {
                                "stage" : "IXSCAN",
                                "keyPattern" : {
                                        "_id" : 1
                                },
                                "indexName" : "_id_",
                                "isMultiKey" : false,
                                "multiKeyPaths" : {
                                        "_id" : [ ]
                                },
                                "isUnique" : true,
                                "isSparse" : false,
                                "isPartial" : false,
                                "indexVersion" : 2,
                                "direction" : "forward",
                                "indexBounds" : {
                                        "_id" : [
                                                "[MinKey, MaxKey]"
                                        ]
                                }
                        }
                },
                "rejectedPlans" : [ ]
        },
        "ok" : 1
}
