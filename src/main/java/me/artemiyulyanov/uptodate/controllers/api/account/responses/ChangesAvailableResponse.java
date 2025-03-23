package me.artemiyulyanov.uptodate.controllers.api.account.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class ChangesAvailableResponse extends ServerResponse<Void> {
    private List<ConflictedColumn> conflictedColumns;
    private Boolean changesAvailable;

    @JsonIgnore
    @Override
    public Void getResponse() {
        return super.getResponse();
    }

    @JsonIgnore
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public enum ConflictedColumn {
        EMAIL, USERNAME;
    }
}