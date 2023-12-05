package com.aurora.juhe.service;

import com.aurora.juhe.model.dto.JuheNetworkhotResult;
import com.aurora.juhe.model.dto.JuheSoupResult;

/**
 * @author jonk
 * @Description:
 * @date 24/5/2023 下午2:05
 */
public interface JuheService {

    public JuheNetworkhotResult queryNetworkhot();

    JuheSoupResult soup();
}
