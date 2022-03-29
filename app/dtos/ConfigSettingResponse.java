package dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfigSettingResponse {

    private String key;
    private String name;
    private String module;
    private String value;

}
