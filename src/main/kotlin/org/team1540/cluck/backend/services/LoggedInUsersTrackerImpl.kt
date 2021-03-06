package org.team1540.cluck.backend.services

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.team1540.cluck.backend.data.User
import org.team1540.cluck.backend.data.UserRepository
import org.team1540.cluck.backend.interfaces.LoggedInUsersTracker

@Service
class LoggedInUsersTrackerImpl : LoggedInUsersTracker {

    private val logger = KotlinLogging.logger {}

    @Autowired
    lateinit var users: UserRepository

    override fun getLoggedInUsers(): Map<String, String> {
        // TODO: Find a better way of doing this than iterating through every user
        logger.debug { "Processing request for logged in users" }
        return users.findAllByInNow(true)
                .associate { user ->
                    user.name to (user.lastEvent?.toString()
                            ?: user.clockEvents.sortedBy { it.timestamp }.last().timestamp.toString())
                }
                .also { logger.debug { "${it.size} logged-in users found" } }
    }

    override fun isUserLoggedIn(user: User) = user.inNow
            ?: user.clockEvents.maxBy { it.timestamp }?.clockingIn ?: false
}
