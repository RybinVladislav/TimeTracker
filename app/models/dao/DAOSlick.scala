package models.dao

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait DAOSlick extends DBTableDefinitions with HasDatabaseConfigProvider[JdbcProfile]
