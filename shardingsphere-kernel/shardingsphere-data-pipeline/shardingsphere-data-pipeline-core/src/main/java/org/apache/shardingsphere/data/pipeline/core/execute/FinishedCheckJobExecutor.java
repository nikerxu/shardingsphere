/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.data.pipeline.core.execute;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.data.pipeline.api.executor.AbstractLifecycleExecutor;
import org.apache.shardingsphere.data.pipeline.core.api.PipelineAPIFactory;
import org.apache.shardingsphere.data.pipeline.core.job.FinishedCheckJob;
import org.apache.shardingsphere.data.pipeline.scenario.rulealtered.RuleAlteredContext;
import org.apache.shardingsphere.data.pipeline.spi.rulealtered.RuleAlteredJobCompletionDetectAlgorithm;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;

/**
 * Finished check job executor.
 */
@Slf4j
public final class FinishedCheckJobExecutor extends AbstractLifecycleExecutor {
    
    private static final String JOB_NAME = "_finished_check";
    
    private static final String CRON_EXPRESSION = "0 * * * * ?";
    
    @Override
    public void start() {
        super.start();
        RuleAlteredJobCompletionDetectAlgorithm clusterAutoSwitchAlgorithm = RuleAlteredContext.getInstance().getClusterAutoSwitchAlgorithm();
        if (null == clusterAutoSwitchAlgorithm) {
            log.info("clusterAutoSwitchAlgorithm not configured, auto switch will not be enabled. You could query migration progress and switch manually with DistSQL.");
            return;
        }
        log.info("Start finished check job executor.");
        new ScheduleJobBootstrap(PipelineAPIFactory.getRegistryCenter(), new FinishedCheckJob(), createJobConfig()).schedule();
    }
    
    private JobConfiguration createJobConfig() {
        return JobConfiguration.newBuilder(JOB_NAME, 1).cron(CRON_EXPRESSION).build();
    }
}
