import { Component } from '@angular/core';
import { Header } from '../../components/header/header';
import { RouterOutlet } from '@angular/router';
import { SidebarLeft } from '../../components/sidebar-left/sidebar-left';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, Header, SidebarLeft],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss'
})
export class MainLayout {

}
