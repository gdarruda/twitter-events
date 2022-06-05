package database

import io.getquill._

object SQLite {
    
    lazy val ctx = new SqliteJdbcContext(SnakeCase, "ctx")
    import ctx._    

    def loadProfiles = run(quote {query[Profile]})
    
    def loadProfile(name: String) = run(
        quote  { query[Profile].filter(p => p.username == lift(name)) } 
    ) 

}