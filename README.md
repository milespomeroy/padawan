# Padawan

### Example implementation of using Postgres Arrays with [JDBI](http://jdbi.org).

Based on [In Clauses](http://skife.org/jdbi/java/2011/12/21/jdbi_in_clauses.html) blog post by Brian McCallister.

```bash
git clone https://github.com/milespomeroy/padawan
cd padawan
mvn -Djetty.port=9999 jetty:run
```

Then you should see "10" if you go to <http://localhost:9999>.

I didn't really do anything different than the example that Brian gave, except for registering on the DBI instead of the Handle.

