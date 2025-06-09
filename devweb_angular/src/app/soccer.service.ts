export interface LocalTime {
    hour: number;
    minute: number;
}

export interface Team {
    id: string;
    name: string;
}

export interface RankingRow {
    team: Team;
    rank: number;
    matchPlayedCount: number;
    matchWonCount: number;
    matchLostCount: number;
    drawCount: number;
    goalForCount: number;
    goalAgainstCount: number;
    goalDifference: number;
    points: number;

}

export interface Match {
    id: string;
    homeTeam: Team;
    awayTeam: Team;
    homeTeamGoals: number;
    awayTeamGoals: number;
    date: string;
    time: LocalTime;
}

import {Injectable} from '@angular/core';

import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class SoccerService {
    constructor(private http: HttpClient) {
    }

    getRanking(): Observable<RankingRow[]> {
        return this.http.get<RankingRow[]>('http://localhost:8080/api/ranking');
    }

    getRankingRow(teamId: string): Observable<RankingRow> {
        return this.http.get<RankingRow>(`http://localhost:8080/api/teams/${teamId}/ranking`);
    }

    getTeamMatches(teamId: string): Observable<Match[]> {
        return this.http.get<Match[]>(`http://localhost:8080/api/teams/${teamId}/matches`);
    }
}
