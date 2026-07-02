package com.radonshadow.focusdrift.domain.usecase.timer

import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import javax.inject.Inject

class ResumeSessionUseCase @Inject constructor(
    private val timerController: FocusTimerController
) {
    operator fun invoke() = timerController.resume()
}
