package com.ranseo.valendar.domain

import com.ranseo.valendar.data.repo.LandFcstRepository
import javax.inject.Inject

class GetLandFcstUseCase @Inject constructor(repository: LandFcstRepository) {
    val landFcstGetter: suspend (numOfRows: Int, pageNo: Int, baseDate: Int, baseTime: Int, nx: String, ny: String) -> Unit =
        { numOfRows, pageNo, baseDate, baseTime, nx, ny ->
            repository.getLandFcst(numOfRows, pageNo, baseDate, baseTime, nx, ny)
        }

    suspend operator fun invoke(numOfRows: Int=14, pageNo: Int=1, baseDate: Int, baseTime: Int, nx: String, ny: String) {
        landFcstGetter(numOfRows, pageNo, baseDate, baseTime, nx,ny)
    }
}