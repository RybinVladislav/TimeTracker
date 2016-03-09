package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.daos.{CacheAuthenticatorDAO, DelegableAuthInfoDAO}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.repositories.DelegableAuthInfoRepository
import com.mohiva.play.silhouette.impl.util.{BCryptPasswordHasher, DefaultFingerprintGenerator, PlayCacheLayer, SecureRandomIDGenerator}
import models.{TimeEntryResponse, User}
import models.dao.entries.{TimeEntryDAO, TimeEntryDAOImpl}
import models.dao.responses.{ResponseDAO, ResponseDAOImpl}
import models.dao.users.{PasswordInfoDAO, UserDAO, UserDAOImpl}
import services.entries.{TimeEntriesService, TimeEntriesServiceImpl}
import services.users.{UsersService, UsersServiceImpl}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.libs.concurrent.Execution.Implicits._
import services.responses.{ResponsesService, ResponsesServiceImpl}

class SilhouetteModule extends AbstractModule with ScalaModule{
  def configure() = {
    bind[UsersService].to[UsersServiceImpl]
    bind[TimeEntriesService].to[TimeEntriesServiceImpl]
    bind[ResponsesService].to[ResponsesServiceImpl]
    bind[UserDAO].to[UserDAOImpl]
    bind[TimeEntryDAO].to[TimeEntryDAOImpl]
    bind[ResponseDAO].to[ResponseDAOImpl]
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[CacheLayer].to[PlayCacheLayer]
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  /**
    * Provides the HTTP layer implementation.
    *
    * @param client Play's WS client.
    * @return The HTTP layer implementation.
    */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
    * Provides the Silhouette environment.
    *
    * @param userService The user service implementation.
    * @param authenticatorService The authentication service implementation.
    * @param eventBus The event bus instance.
    * @return The Silhouette environment.
    */
  @Provides
  def provideEnvironment(userService: UsersService,
                          authenticatorService: AuthenticatorService[JWTAuthenticator],
                          eventBus: EventBus): Environment[User, JWTAuthenticator] = {

    Environment[User, JWTAuthenticator](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  /**
    * Provides the authenticator service.
    *
    * @param idGenerator The ID generator implementation.
    * @param configuration The Play configuration.
    * @param clock The clock instance.
    * @return The authenticator service.
    */
  @Provides
  def provideAuthenticatorService(cacheLayer: CacheLayer,
                                   idGenerator: IDGenerator,
                                   configuration: Configuration,
                                   clock: Clock): AuthenticatorService[JWTAuthenticator] = {

    val config = configuration.underlying.as[JWTAuthenticatorSettings]("silhouette.authenticator")
    new JWTAuthenticatorService(config, Some(new CacheAuthenticatorDAO[JWTAuthenticator](cacheLayer)), idGenerator, clock)
  }

  /**
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {

    new DelegableAuthInfoRepository(passwordInfoDAO)
  }

  /**
    * Provides the credentials provider.
    *
    * @param authInfoRepository The auth info repository implementation.
    * @param passwordHasher The default password hasher implementation.
    * @return The credentials provider.
    */
  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                  passwordHasher: PasswordHasher): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))
  }
}
