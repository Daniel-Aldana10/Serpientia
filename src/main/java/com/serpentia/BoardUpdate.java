package com.serpentia;

import com.serpentia.BoardState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdate {
    private BoardState board;
}