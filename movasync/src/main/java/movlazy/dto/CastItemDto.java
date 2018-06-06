/*
 * Copyright (c) 2018 Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package movlazy.dto;

/**
 * @author Miguel Gamboa
 *         created on 04-08-2017
 */
public class CastItemDto {
    private String character;
    private String name;
    private String credit_id;
    private int id;

    public CastItemDto(String character, String name, String credit_id, int id){
        this.character = character;
        this.name = name;
        this.credit_id = credit_id;
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditId() {
        return credit_id;
    }

    public void setCreditId(String credit_id) {
        this.credit_id = credit_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CastItemDto{" +
                " character='" + character +
                " name='" + name +
                ", credit_id='" +  credit_id + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
