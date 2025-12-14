import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router";
import "./App.css";
import { Box, Button } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

const ActivitiesPage = () => {
  return (
    <Box
      component="section"
      sx={{
        p: 2,
        border: "1px dashed grey",
        backgroundColor: "white",
        width: "600px",
      }}
    >
      <ActivityForm onActivitiesAdded={() => window.location.reload()} />
      <ActivityList />
    </Box>
  );
};

function App() {
  const { token, tokenData, logIn, logOut, isAuthenticated } =
    useContext(AuthContext);

  const dispatch = useDispatch();

  const [authReady, setAuthReady] = useState(false);

  useEffect(() => {
    if (token) {
      dispatch(
        setCredentials({
          user: tokenData,
          token: token,
          userId: tokenData?.sub,
        })
      );
    }
  }, [token, tokenData, dispatch]);

  return (
    <Router>
      {!token ? (
        <Button
          variant="contained"
          color="primary"
          onClick={() => {
            logIn();
          }}
        >
          login
        </Button>
      ) : (
        <Box component="section" sx={{ p: 2, border: "1px dashed grey" }}>
          <Routes>
            <Route path="/activities" element={<ActivitiesPage />} />

            <Route path="/activities/:id" element={<ActivityDetail />} />
            <Route
              path="/"
              element={token ? <Navigate to="/activities" replace /> : logIn}
            />
          </Routes>
        </Box>
      )}
    </Router>
  );
}

export default App;
