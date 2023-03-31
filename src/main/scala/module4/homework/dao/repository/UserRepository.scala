package module4.homework.dao.repository

import io.getquill.context.ZioJdbc._
import io.getquill.{EntityQuery, Quoted}
import module4.homework.dao.entity._
import module4.phoneBook.db
import zio.{Has, ULayer, ZLayer}

object UserRepository{


    val dc = db.Ctx
    import dc._

    type UserRepository = Has[Service]

    trait Service{
        def findUser(userId: UserId): QIO[Option[User]]
        def createUser(user: User): QIO[User]
        def createUsers(users: List[User]): QIO[List[User]]
        def updateUser(user: User): QIO[Unit]
        def deleteUser(user: User): QIO[Unit]
        def findByLastName(lastName: String): QIO[List[User]]
        def list(): QIO[List[User]]
        def userRoles(userId: UserId): QIO[List[Role]]
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): QIO[Unit]
        def listUsersWithRole(roleCode: RoleCode): QIO[List[User]]
        def findRoleByCode(roleCode: RoleCode): QIO[Option[Role]]
    }

    class ServiceImpl extends Service{

        lazy val userSchema: Quoted[EntityQuery[User]] = quote {
            querySchema[User](""""User"""")
        }

        lazy val roleSchema = quote {
            querySchema[Role](""""Roles"""")
        }
        lazy val userToRoleSchema = quote {
            querySchema[UserToRole](""""UserRoles"""")
        }

        def findUser(userId: UserId): Result[Option[User]] = run(userSchema.filter(_.id == lift(userId.id)))
          .map(_.headOption)

        def createUser(user: User): Result[User] = run(userSchema.insert(lift(user))).as(user)

        def createUsers(users: List[User]): Result[List[User]] = run(
            for (elem <- users)
                userSchema.insert(lift(elem))
        ).as(users)

        def updateUser(user: User): Result[Unit] = run(userSchema.update(lift(user))).unit

        def deleteUser(user: User): Result[Unit] = run(userSchema.filter(_.id == lift(user.id)).delete).unit

        def findByLastName(lastName: String): Result[List[User]] = run(userSchema.filter(_.lastName == lift(lastName)))

        def list(): Result[List[User]] = run(userSchema)

        def userRoles(userId: UserId): Result[List[Role]] = run(
            roleSchema.join(userToRoleSchema).on(_.code==_.roleId).nested.filter {
                case (_, v) => v.userId==lift(userId.id)
            }
        )

        def insertRoleToUser(roleCode: RoleCode, userId: UserId): Result[Unit] = run(
            userToRoleSchema.filter(_.userId==lift(userId)).insert(lift(UserToRole.apply(roleCode.code, userId.id)))
        )

        def listUsersWithRole(roleCode: RoleCode): Result[List[User]] = run(
            userSchema.join(userToRoleSchema).on(_.id==_.userId).nested.filter{
                case (_, v) => v.roleId==roleCode.code
            })
        def findRoleByCode(roleCode: RoleCode): Result[Option[Role]] = run(
            roleSchema.filter(_.code==lift(roleCode.code))
        )

    }

    val live: ULayer[UserRepository] = ZLayer.succeed(new ServiceImpl)


}