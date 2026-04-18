package com.wasel.repository;
import com.wasel.entity.Report;
import com.wasel.model.IncidentCategory;
import com.wasel.model.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/*ReportRepository is an interface that allows us to interact with the reports table in the database.
It gives us ready-to-use methods like save(), findAll(), findById(), and delete().
We don’t need to write SQL because Spring Data JPA and Hibernate handle it automatically.
If we want custom queries, we can just add method names, and Spring will generate the SQL.*/
//يخبر Spring أن هذا Interface للتعامل مع قاعدة البيانات (@Repository)
@Repository

public interface ReportRepository extends JpaRepository<Report, Long>
{
    // This query counts how many reports the specific user submitted after a certain time.
    // تحسب كل التقارير اللي صارت بعد الوقت اللي تمرره
    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdBy.id = :userId AND r.timestamp > :since")
    long countRecentByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // This query counts how many reports a specific user has submitted after a certain time (since)
    // and within a small geographic area around a given latitude and longitude.
    // In other words, it checks for recent reports by the user that are geographically close to a certain point.
    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdBy.id = :userId " +
            "AND r.timestamp > :since " +
            "AND ABS(r.latitude - :latitude) < 0.001 " +
            "AND ABS(r.longitude - :longitude) < 0.001")
    long countNearbyRecentByUser(@Param("userId") Long userId,
                                 @Param("since") LocalDateTime since,
                                 @Param("latitude") Double latitude,
                                 @Param("longitude") Double longitude);

     //Retrieves all reports with optional filters for status and category.
     //Returns results paginated and sorted by newest first.
    @Query("SELECT r FROM Report r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:category IS NULL OR r.category = :category) " +
            "ORDER BY r.timestamp DESC")
    Page<Report> findAllWithFilters(
            @Param("status") ReportStatus status,
            @Param("category") IncidentCategory category,
            Pageable pageable);

    List<Report> findByCategoryAndStatusInAndLatitudeBetweenAndLongitudeBetweenAndTimestampAfter(
            IncidentCategory category,
            List<ReportStatus> statuses,
            Double latMin, Double latMax,
            Double lonMin, Double lonMax,
            LocalDateTime threshold
    );

    /**
     * Native query — Query 2: Nearby Duplicate Reports
     *
     * Finds reports of the same category submitted within a geographic radius
     * (using the Haversine great-circle distance formula) and within a recent
     * time window.  LEAST(1.0, ...) guards against floating-point values
     * slightly above 1 that would make acos return NaN.
     *
     * Parameters:
     *   latitude    – centre point latitude
     *   longitude   – centre point longitude
     *   radiusKm    – search radius in kilometres
     *   category    – incident category name (e.g. "ACCIDENT")
     *   withinMinutes – look-back window in minutes
     *
     * Used by: Duplicate detection to improve accuracy beyond bounding-box checks.
     */
    @Query(value = """
            SELECT r.*
            FROM reports r
            WHERE r.category = :category
              AND r.timestamp >= NOW() - (:withinMinutes * INTERVAL '1 minute')
              AND (
                    -- Haversine formula: straight-line distance on Earth's surface
                    6371.0 * acos(
                        LEAST(1.0,
                            cos(radians(:latitude))  * cos(radians(r.latitude))
                            * cos(radians(r.longitude) - radians(:longitude))
                            + sin(radians(:latitude)) * sin(radians(r.latitude))
                        )
                    )
                  ) <= :radiusKm
            ORDER BY r.timestamp DESC
            """,
            nativeQuery = true)
    List<Report> findNearbyReports(
            @Param("latitude")     double latitude,
            @Param("longitude")    double longitude,
            @Param("radiusKm")     double radiusKm,
            @Param("category")     String category,
            @Param("withinMinutes") int withinMinutes
    );
}
