package com.feblic.secure.events;

import com.feblic.secure.models.BaseEntityModel;
import com.feblic.secure.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import javax.servlet.http.HttpSession;
import java.util.Date;

public class BaseModelListener extends AbstractMongoEventListener<BaseEntityModel> {

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    HttpSession session;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseEntityModel> event) {

        if (event.getSource().getId() == null || event.getSource().getId() < 1) {
            event.getSource().setCreatedAt(new Date());
            event.getSource().setId(sequenceGenerator.generateSequence(event.getSource().getSequenceName()));

            if(session.getAttribute("user-id") != null) {
                event.getSource().setCreatedById((Long) session.getAttribute("user-id"));
            }
        }

        event.getSource().setUpdatedAt(new Date());

        if(session.getAttribute("user-id") != null) {
            event.getSource().setUpdatedById((Long) session.getAttribute("user-id"));
        }
    }
}
