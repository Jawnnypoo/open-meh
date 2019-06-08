package com.jawnnypoo.openmeh.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

/**
 * Creates the jobs.
 */
class MehJobCreator: JobCreator {

    override fun create(tag: String): Job? {
        when(tag) {
            ReminderJob.TAG -> return ReminderJob()
        }
        return null
    }
}