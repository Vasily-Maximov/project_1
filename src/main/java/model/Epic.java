package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends AbstractTask {

    private final List<SubTask> subTasksOfEpic = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(TaskType.EPIC, name, description);
    }

    private List<Integer> getIdOfSubtasks() {
        List<Integer> listId = new ArrayList<>();
        for (SubTask subTask : subTasksOfEpic) {
            listId.add(subTask.getId());
        }
        return listId;
    }

    private static int compareStartTime(SubTask subTask1, SubTask subTask2) {
        return subTask1.getStartTime().compareTo(subTask2.getStartTime());
    }

    private static int compareEndTime(SubTask subTask1, SubTask subTask2) {
        return subTask1.getEndTime().get().compareTo(subTask2.getEndTime().get());
    }

    private TaskStatus calculateStatus() {
        TaskStatus taskStatus;
        int amountNewTask = 0;
        int amountDoneTask = 0;
        for (SubTask task : subTasksOfEpic) {
            switch (task.getTaskStatus()) {
                case NEW:
                    amountNewTask++;
                    break;
                case DONE:
                    amountDoneTask++;
                    break;
                default:
            }
        }
        if (subTasksOfEpic.size() == amountNewTask) {
            taskStatus = TaskStatus.NEW;
        } else if (subTasksOfEpic.size() == amountDoneTask) {
            taskStatus = TaskStatus.DONE;
        } else {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
        return taskStatus;
    }

    public LocalDateTime calculateStartTimeForEpic() {
        Optional<SubTask> optionalSubTask = subTasksOfEpic.stream().filter(subTask -> subTask.getStartTime() != null)
                .min(Epic::compareStartTime);
        return optionalSubTask.map(AbstractTask::getStartTime).orElse(null);
    }

    public void calculateEndTimeForEpic() {
        Optional<SubTask> optionalSubTask = subTasksOfEpic.stream().filter(subTask -> subTask.getEndTime().isPresent())
                .max(Epic::compareEndTime);
        endTime = optionalSubTask.flatMap(AbstractTask::getEndTime).orElse(null);
        if (endTime != null) {
            endTime = endTime.plusMinutes(calculateDurationForEpic());
        }
    }

    public long calculateDurationForEpic() {
        return subTasksOfEpic.stream().mapToLong(AbstractTask::getDuration).sum();
    }

    public List<SubTask> getSubTasksOfEpic() {
        return subTasksOfEpic;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        taskStatus = calculateStatus();
        super.setTaskStatus(taskStatus);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        if (endTime != null) {
            return Optional.of(endTime);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        startTime = calculateStartTimeForEpic();
        super.setStartTime(startTime);
    }

    @Override
    public void setDuration(long duration) {
        duration = calculateDurationForEpic();
        super.setDuration(duration);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + getId() + '\'' +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getTaskStatus() +
                ", subTasksOfEpic=" + getIdOfSubtasks() +
                '}';
    }
}