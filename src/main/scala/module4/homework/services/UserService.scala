package module4.homework.services

import module4.homework.dao.entity.{Role, RoleCode, User}
import module4.homework.dao.repository.UserRepository
import module4.phoneBook.db
import zio.{Has, RIO, ZLayer}
import zio.macros.accessible

@accessible
object UserService{
    type UserService = Has[Service]

    trait Service{
        def listUsers(): RIO[db.DataSource, List[User]]
        def listUsersDTO(): RIO[db.DataSource, List[UserDTO]]
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[db.DataSource, UserDTO]
        def listUsersWithRole(roleCode: RoleCode): RIO[db.DataSource, List[UserDTO]]
    }

    class Impl(userRepo: UserRepository.Service) extends Service{
        val dc = db.Ctx

        def listUsers(): RIO[db.DataSource, List[User]] =
        userRepo.list()


        def listUsersDTO(): RIO[db.DataSource,List[UserDTO]] =
            listUsers().map(_.map(x=> UserDTO(x,userRepo.userRoles(x.typedId).as[Set[Role]])))
        
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[db.DataSource, UserDTO] =  dc.transaction(
            for{
                ex <- userRepo.createUser(user)
                _ <- userRepo.insertRoleToUser(roleCode, ex.typedId)
            } yield UserDTO(ex, userRepo.userRoles(ex.typedId).as[Set[Role]])
        )
        
        def listUsersWithRole(roleCode: RoleCode): RIO[db.DataSource,List[UserDTO]] =
            listUsersDTO().map(x => x.filter(_.roles.filter(_.code==roleCode.code).head.code==roleCode.code))
        
        
    }

    val live: ZLayer[UserRepository.UserRepository, Nothing, UserService] =
        ZLayer.fromService[UserRepository.Service, UserService.Service](addressRepo =>
            new Impl( addressRepo))
}

case class UserDTO(user: User, roles: Set[Role])