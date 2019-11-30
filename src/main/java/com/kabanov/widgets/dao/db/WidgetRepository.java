package com.kabanov.widgets.dao.db;

import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Repository
public interface WidgetRepository extends JpaRepository<Widget, UUID> {

    Slice<Widget> findAllByStartPointSum(Pageable pageable);

  /*  @Nullable
    Widget findTop1ByZIndex();*/

    @Nullable
    Widget findOneByZIndex(Integer index);

    /*
    For this query I have extracted table name and field name into constant. Such code a bit harder to read,
    but the great benefit we get - is we don't use inline fields name. And if one day the field/table name will be changed, 
    we need to change only the constant value. 
     */
    
    /*@Query(value =
            "UPDATE " + Widget.TABLE_NAME +
                    "SET " + Z_INDEX_COLUMN_NAME + " = " + Z_INDEX_COLUMN_NAME + " + 1" +
                    "WHERE " + Z_INDEX_COLUMN_NAME + " >= :zIndex" +
                    "AND " + Z_INDEX_COLUMN_NAME + " <= (" +
                    "SELECT " + Widget.TABLE_NAME + "." + Z_INDEX_COLUMN_NAME

            ")"

    )*/

    /*@Query(
            "SELECT t1.zIndex " +
                    "FROM WIDGET as q1 LEFT JOIN queue AS q2 ON q1.position + 1 = q2.position " +
                    "WHERE q2.name is NULL AND q1.position > 3 " +
                    "ORDER BY q1.position " +
                    "LIMIT 1"
    )*/
    /*@Query(
            "SELECT t1.zIndex " +
                    "FROM WIDGET as t1 LEFT JOIN WIDGET AS t2 ON t1.zIndex + 1 = t2.zIndex " +
                    "WHERE t2.zIndex is NULL AND t1.zIndex > :zIndex " +
                    "ORDER BY t1.zIndex"
    )
    int findTheFirstGapFrom(@Param("zIndex") int from);*/
}
