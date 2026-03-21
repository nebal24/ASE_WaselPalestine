package com.wasel.repository;
import com.wasel.entity.Report;
import com.wasel.model.Category;
import com.wasel.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*ReportRepository is an interface that allows us to interact with the reports table in the database.
It gives us ready-to-use methods like save(), findAll(), findById(), and delete().
We don’t need to write SQL because Spring Data JPA and Hibernate handle it automatically.
If we want custom queries, we can just add method names, and Spring will generate the SQL.*/
//يخبر Spring أن هذا Interface للتعامل مع قاعدة البيانات (@Repository)
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>
                                                      //Report →نوع الانتيتي الي رح نتعامل معه
                                                     //نوع primary key = repory_id
{   List<Report> findByCategoryAndStatusInAndLatitudeBetweenAndLongitudeBetweenAndTimestampAfter(
        Category category,
        List<ReportStatus> statuses,
        Double latMin, Double latMax,
        Double lonMin, Double lonMax,
        LocalDateTime threshold
);

}