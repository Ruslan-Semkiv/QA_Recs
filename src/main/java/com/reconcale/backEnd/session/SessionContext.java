package com.reconcale.backEnd.session;

import com.reconcale.backEnd.entity.Customer;
import com.reconcale.backEnd.entity.CustomerVisit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class SessionContext {
    private Customer userInAction;
    private CustomerVisit visitInAction;
}