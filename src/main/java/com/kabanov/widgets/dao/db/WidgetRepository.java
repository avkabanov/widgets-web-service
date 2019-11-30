package com.kabanov.widgets.dao.db;

import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kabanov.widgets.domain.Widget;

/**
 * @author Kabanov Alexey
 */
@Repository
public interface WidgetRepository extends JpaRepository<Widget, UUID> {

    @Nullable
    Widget findOneByZIndex(Integer index);
}
