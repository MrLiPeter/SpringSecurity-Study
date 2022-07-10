package uaa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uaa.domain.MfaType;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTotpDto {
    @NotNull
    private String MfaId;

    @NotNull
    private MfaType mfaType;
}
