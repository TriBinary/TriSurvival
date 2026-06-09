package net.trilleo.mc.plugins.trisurvival.data

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import java.util.*

object SkillDAO {

    fun loadAll(uuid: UUID): Map<Skill, Pair<Double, Int>> {
        val result = EnumMap<Skill, Pair<Double, Int>>(Skill::class.java)
        val conn = DatabaseManager.getConnection()
        conn.prepareStatement("SELECT skill, xp, level FROM player_skills WHERE uuid = ?").use { stmt ->
            stmt.setString(1, uuid.toString())
            val rs = stmt.executeQuery()
            while (rs.next()) {
                val skill = runCatching { Skill.valueOf(rs.getString("skill")) }.getOrNull() ?: continue
                result[skill] = Pair(rs.getDouble("xp"), rs.getInt("level"))
            }
        }
        return result
    }

    fun save(uuid: UUID, skill: Skill, xp: Double, level: Int) {
        val conn = DatabaseManager.getConnection()
        conn.prepareStatement(
            """
            INSERT INTO player_skills (uuid, skill, xp, level) VALUES (?, ?, ?, ?)
            ON CONFLICT(uuid, skill) DO UPDATE SET xp = excluded.xp, level = excluded.level
            """.trimIndent()
        ).use { stmt ->
            stmt.setString(1, uuid.toString())
            stmt.setString(2, skill.name)
            stmt.setDouble(3, xp)
            stmt.setInt(4, level)
            stmt.executeUpdate()
        }
    }

    fun saveAll(uuid: UUID, data: Map<Skill, Pair<Double, Int>>) {
        val conn = DatabaseManager.getConnection()
        conn.prepareStatement(
            """
            INSERT INTO player_skills (uuid, skill, xp, level) VALUES (?, ?, ?, ?)
            ON CONFLICT(uuid, skill) DO UPDATE SET xp = excluded.xp, level = excluded.level
            """.trimIndent()
        ).use { stmt ->
            for ((skill, pair) in data) {
                stmt.setString(1, uuid.toString())
                stmt.setString(2, skill.name)
                stmt.setDouble(3, pair.first)
                stmt.setInt(4, pair.second)
                stmt.addBatch()
            }
            stmt.executeBatch()
        }
    }
}
