package tw.borishuang.sumishisen.navigation

sealed class Screens {
    data object Home : Screens()

    data object MiniGameSolver : Screens()

    data object MiniGameResult : Screens()
}