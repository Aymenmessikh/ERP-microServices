package com.example.auditlogservice.Config.filter.clause;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private List<Clause> clauses1;
    private List<Clause> clauses2;
}
