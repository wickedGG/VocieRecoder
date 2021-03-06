/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yb.com.vocieRecoder.util

import yb.com.vocieRecoder.model.repository.AdapterRepository
import yb.com.vocieRecoder.model.repository.PlayerRepository
import yb.com.vocieRecoder.model.repository.RecorderRepository
import yb.com.vocieRecoder.util.viewmodels.AdapterViewModel
import yb.com.vocieRecoder.util.viewmodels.TrainingViewModel

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepository.getInstance()
    }

    private fun provideRecorderRepository(): RecorderRepository {
        return RecorderRepository.getInstance()
    }

    fun provideAdapterViewModel(repository: AdapterRepository)
            : AdapterViewModel {
        return AdapterViewModel(repository)
    }


    fun provideTrainingViewModel(): TrainingViewModel.Factory {
        return TrainingViewModel.Factory(providePlayerRepository(), provideRecorderRepository())
    }

}