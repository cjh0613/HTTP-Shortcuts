package ch.rmy.android.http_shortcuts.plugin

import android.content.Context
import ch.rmy.android.http_shortcuts.activities.ExecuteActivity
import ch.rmy.android.http_shortcuts.plugin.VariableHelper.extractVariableMap
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultUnknown
import java.util.concurrent.TimeoutException

class TriggerShortcutActionRunner : TaskerPluginRunnerActionNoOutput<Input>() {
    override fun run(context: Context, input: TaskerInput<Input>): TaskerPluginResult<Unit> {
        val shortcutId = input.regular.shortcutId
        val variableValues = extractVariableMap(input)
        ExecuteActivity.IntentBuilder(context, shortcutId)
            .variableValues(variableValues)
            .startActivity(context)

        return try {
            // TODO: This is a nasty hack, I'm sorry. Let's say this is an experiment for now...
            // I hope to find a better way to monitor whether the request is still in progress
            SessionMonitor.startAndMonitorSession(TIMEOUT)
            TaskerPluginResultSucess()
        } catch (e: TimeoutException) {
            TaskerPluginResultUnknown()
        }
    }

    companion object {
        private const val TIMEOUT = 30000
    }
}
