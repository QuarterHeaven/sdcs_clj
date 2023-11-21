# Introduction to sdcs_clj

sdcs_clj is a simple implement of Distributed System [Course Experiment](https://uestc.feishu.cn/docx/C7ajdHwq9oppWXxhyelcLVvHngc) created by Clojure.

# Usage

Firstly clone this repository:

```
git clone https://github.com/QuarterHeaven/sdcs_clj.git --depth 1 && cd sdcs_clj
```

Then use `docker compose` to run it:

```
docker compose up
```

and it will run 3 distributed node listening from port 9527 to 9532. You can use `curl -XPOST/GET/-XDELETE` to test them:

```
curl -XPOST -H "Content-type: application/json" http://127.0.0.1:9527/ -d '{"myname": "电子科技大学@2023"}'
curl -XPOST -H "Content-type: application/json" http://127.0.0.1:9528/ -d '{"tasks": ["task 1", "task 2", "task 3"]}'
curl -XPOST -H "Content-type: application/json" http://127.0.0.1:9529/ -d '{"age": 123}'

curl http://127.0.0.1:9529/myname
{"myname": "电子科技大学@2023"}

curl http://127.0.0.1:9527/tasks
{"tasks": ["task 1", "task 2", "task 3"]}

curl http://127.0.0.1:9528/notexistkey
# 404, not found

curl -XDELETE http://127.0.0.1:9529/myname
1

curl http://127.0.0.1:9527/myname
# 404, not found

curl -XDELETE http://127.0.0.1:9529/myname
0
```

Notice that only port 9527 to 9529 are for HTTP requests so don't use them on other ports. Port 9530 to 9532 are for inner rpc use.

You can also test it using [the testsuit](https://github.com/ruini-classes/sdcs-testsuit).
