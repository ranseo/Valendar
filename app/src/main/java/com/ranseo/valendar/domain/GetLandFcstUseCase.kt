package com.ranseo.valendar.domain

import com.ranseo.valendar.data.repo.LandFcstRepository
import javax.inject.Inject

class GetLandFcstUseCase @Inject constructor(repository: LandFcstRepository) {
    val landFcstGetter : suspend ()->Unit = {
        repository.getLandFcst()
    }

    suspend operator fun invoke() {
        landFcstGetter()
    }
}