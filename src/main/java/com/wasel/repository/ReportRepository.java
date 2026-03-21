package com.wasel.repository;
import com.wasel.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

/*ReportRepository is an interface that allows us to interact with the reports table in the database.
It gives us ready-to-use methods like save(), findAll(), findById(), and delete().
We don’t need to write SQL because Spring Data JPA and Hibernate handle it automatically.
If we want custom queries, we can just add method names, and Spring will generate the SQL.*/
//يخبر Spring أن هذا Interface للتعامل مع قاعدة البيانات (@Repository)
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer>
        //Report →نوع الانتيتي الي رح نتعامل معه
        //نوع primary key = repory_id
{
    // This query counts how many reports the specific user submitted after a certain time.
    // تحسب كل التقارير اللي صارت بعد الوقت اللي تمرره
    @Query("SELECT COUNT(r) FROM Report r WHERE r.user.id = :userId AND r.timestamp > :since")
    long countRecentByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // This query counts how many reports a specific user has submitted after a certain time (since)
    // and within a small geographic area around a given latitude and longitude.
    // In other words, it checks for recent reports by the user that are geographically close to a certain point.
    @Query("SELECT COUNT(r) FROM Report r WHERE r.user.id = :userId " +
            "AND r.timestamp > :since " +
            "AND ABS(r.latitude - :latitude) < 0.001 " +
            "AND ABS(r.longitude - :longitude) < 0.001")
    long countNearbyRecentByUser(@Param("userId") Long userId,
                                 @Param("since") LocalDateTime since,
                                 @Param("latitude") Double latitude,
                                 @Param("longitude") Double longitude);
}
