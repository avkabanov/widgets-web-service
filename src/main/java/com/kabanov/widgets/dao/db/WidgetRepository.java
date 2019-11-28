package com.kabanov.widgets.dao.db;

import java.util.UUID;

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
    

}
